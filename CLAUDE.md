# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 專案性質

練習用的學習專案：專放動畫電影的影城訂票系統，重點在**高併發下不超賣**。分兩階段：

- **階段一（MVP）**：Spring Boot + React + PostgreSQL，以資料庫層機制（條件更新、唯一約束）保證正確性。不預先引入 Redis／MQ。
- **階段二**：壓測找瓶頸後依序加入 Nginx 限流 → Redis → MQ，每一步記錄前後數據。

設計文件是本專案的主要依據：

- `Design.md`：已定案的設計（流程、規則、資料表、API、逾時機制）。實作前先讀對應章節。
- `Todo.md`：待確認與尚未討論的項目（會員驗證細節、錯誤格式、場次規則、測試與壓測、前端、專案結構等）。碰到這些主題時，不要自行定案，先跟使用者討論。
- 兩份文件互相引用時用了舊檔名（`design.md`、`open-items.md`），實際檔名是 `Design.md`、`Todo.md`。

## 目前程式碼狀態

目前只有 Spring Boot 骨架：`pom.xml`（Spring Boot 4.0.0 parent、只有 `spring-boot-starter-webmvc`，artifactId 仍為 `myproject`）與 `src/main/java/com/example/MyApplication.java`（Hello World）。尚無前端、資料庫、Docker Compose、測試，也不是 git repository。

## 指令

沒有 Maven wrapper，使用系統安裝的 `mvn`：

```bash
mvn spring-boot:run          # 啟動
mvn package                  # 打包成 target/myproject-0.0.1-SNAPSHOT.jar
mvn test                     # 執行測試
mvn test -Dtest=ClassName#method   # 執行單一測試
```

## 架構要點（來自 Design.md）

**部署**：瀏覽器（React SPA）→ Nginx（唯一對外入口，`/api/*` 轉發）→ Spring Boot → PostgreSQL，以 Docker Compose 啟動，後端與 DB 只在內部網路。

**模組化單體**，不採微服務。四個模組：`member`、`movie`、`showtime`、`booking`。模組之間只透過 Service 介面呼叫，**不直接存取其他模組的資料表**。訂票入口設計成介面 `BookingService.createOrder`，MVP 為同步寫 DB，階段二換成 MQ 實作時 Controller 不需改動。

**座位兩層模型**：`seat` 是影廳固定配置；`showtime_seat` 是某場次的某座位，狀態（AVAILABLE／LOCKED／SOLD）與 `order_id` 都在這裡。建立場次時就為每個座位預先產生 `showtime_seat`，訂票是 UPDATE 既有列而非 INSERT。

**正確性保證集中在資料庫**（實作時必須維持）：

- 鎖位：同一交易內對 `showtime_seat` 做 `... SET status='LOCKED' WHERE id IN (...) AND showtime_id=? AND status='AVAILABLE'`，更新列數 ≠ 請求座位數就 rollback（全有或全無）。
- 每會員每場次一筆有效訂單：`orders (member_id, showtime_id) WHERE status IN ('PENDING','PAID')` 的 partial unique index。重複送出也靠這個擋（回 409 `ACTIVE_ORDER_EXISTS` 並附既有 orderId），不另做 Idempotency-Key。
- 付款與逾時釋放都以 `status='PENDING'` 為條件更新，付款還要求 `expires_at > now()`，保證兩者只有一方成功。
- 逾時：`expires_at` 由後端時間 + 15 分鐘（設定值）；排程每分鐘把過期 PENDING 改 CANCELLED，並在同一交易內把座位改回 AVAILABLE。

**業務規則**：單筆最多 4 席（API 層檢查）；開演前 15 分鐘停售（回 422）；下單需登入，瀏覽不需要。

**資訊隱藏**：座位圖 API 每個座位只回傳 `available` 布林值，不得暴露 LOCKED 與 SOLD 的差別或訂位者。座位圖的 `id` 是 `showtime_seat.id`，下單時的 `seatIds` 用這個。

**套件與命名慣例**：每個模組只有一層套件（不建 `dto/`、`internal/` 子套件，因為 package-private 不跨子套件）。只有 Service、DTO、enum 是 `public`；Entity、Repository、Controller 是 package-private。DTO 一律用 `XxxRequest`（client 送進來）／`XxxResponse`（回傳給 client）命名，不用 `Dto` 字尾，以 Java `record` 撰寫；Entity 轉 DTO 用 package-private 的 `XxxResponse.from(entity)`。

**API**：前綴 `/api/v1`，分公開／會員（JWT）／管理員（ADMIN）三類；付款、取消採動作端點（`POST /orders/{id}/pay`、`/cancel`）。完整端點與 `POST /orders` 的狀態碼見 Design.md 第 6 節。

## Git 工作流程

遠端：`origin` = https://github.com/Yunzhe0329/movie-ticket-booking.git，預設分支 `main`。所有改動一律由 Agent 開分支、提交 PR，**由使用者 review 後合併**。

### 規則

- **不直接 push `main`**，也不 merge PR、不 force push `main`。Agent 只能 push 自己開的功能分支。
- 一個 PR 只做一件事（一個模組的一個切片，例如「movie 的 Controller + 對應測試」）。設計文件（`Design.md`、`Todo.md`）的修改獨立成 `docs` PR，且修改前先詢問使用者。
- 使用者工作區中未提交、與本次任務無關的檔案不要加進 commit（用 `git add <具體路徑>`，不用 `git add -A` / `git add .`）。

### 分支命名

`<type>/<scope>-<簡短描述>`，全小寫、用 `-` 連接，例如 `feat/movie-controller`、`fix/booking-seat-count`、`docs/git-workflow`。

`type` 與 commit 相同：`feat`、`fix`、`refactor`、`test`、`docs`、`chore`。`scope` 用模組名（`member`、`movie`、`showtime`、`booking`），跨模組或基礎設施用 `infra`、`db`、`frontend` 等。

### Commit 訊息

沿用 Conventional Commits，描述用中文：`<type>(<scope>): <做了什麼>`，例如 `feat(movie): 新增電影列表 API`。一個 commit 一個邏輯改動；body 說明「為什麼」。

### Agent 提交 PR 的步驟

1. 同步：`git switch main && git pull --ff-only`
2. 開分支：`git switch -c feat/movie-controller`
3. 改動並 commit（可多個 commit）
4. 驗證：在 `backend/` 執行 `mvn test`，失敗不得開 PR（若有已知失敗，必須寫進 PR 說明）
5. 推送：`git push -u origin <branch>`
6. 開 PR：`gh pr create --base main --title "<同 commit 格式>" --body "..."`，PR 內容包含：
   - **摘要**：改了什麼
   - **為什麼**：對應 `Design.md` 哪一節或 `Todo.md` 哪一項
   - **如何驗證**：跑了哪些測試、手動測了哪些 API
   - **學習重點**：這次改動中值得理解的概念（使用者是 junior，PR 也是學習材料）
7. 回報 PR 連結，等使用者 review。收到修改意見時，在**同一分支**追加 commit 再 push，不開新 PR。

### 合併後

使用者在 GitHub 以 **Squash and merge** 合併並刪除遠端分支。Agent 下次開工前：

```bash
git switch main && git pull --ff-only
git branch -d <已合併的分支>
```
