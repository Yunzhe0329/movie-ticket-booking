/**
 * 訂票模組：選位鎖位、訂單、付款、逾時釋放。
 *
 * <p>擁有的資料表：orders、showtime_seat。
 * 其他模組不得直接存取這兩張表，需透過本模組的 public Service 介面。
 */
package com.example.ticket.booking;