package com.konggogi.veganlife.support.restassured;


import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class DatabaseClearExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        DatabaseCleaner databaseCleaner = getDatabaseCleaner(extensionContext);
        databaseCleaner.clear();
    }

    private DatabaseCleaner getDatabaseCleaner(ExtensionContext extensionContext) {

        return SpringExtension.getApplicationContext(extensionContext)
                .getBean(DatabaseCleaner.class);
    }
}
