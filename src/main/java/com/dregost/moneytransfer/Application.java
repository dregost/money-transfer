package com.dregost.moneytransfer;

import lombok.SneakyThrows;

public class Application {
    @SneakyThrows
    public static void main(String[] args) {
        new ApplicationServer(8080).start();
    }
}
