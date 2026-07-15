package com.kaan.tracelab.testcase;

public enum TestCaseType {
    FUNCTIONAL, // ozelligin beklendigi gibi calisip calismadigini dogrular
    NEGATIVE,   // hatali veya gecersiz girdiler karsisindaki davranisi test eder
    REGRESSION, // onceden calisan islevlerin yeni degisikliklerle bozulmadigini dogrular
    INTEGRATION // birden fazla bilesenin birlikte calismasi test edilir
}