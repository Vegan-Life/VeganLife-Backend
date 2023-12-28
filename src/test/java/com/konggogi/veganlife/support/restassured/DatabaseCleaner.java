package com.konggogi.veganlife.support.restassured;


import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseCleaner {

    @PersistenceContext private EntityManager entityManager;
    private final List<String> tableNames = new ArrayList<>();

    @PostConstruct
    @SuppressWarnings("unchecked")
    private void findDatabaseTableNames() {
        List<Object[]> tableInfos = entityManager.createNativeQuery("SHOW TABLES").getResultList();
        tableInfos.forEach(tableInfo -> tableNames.add((String) tableInfo[0]));
    }

    @Transactional
    public void clear() {
        entityManager.clear();
        truncate();
    }

    private void truncate() {
        // 제약조건 무효화
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        // truncate 명령 실행
        tableNames.forEach(
                tableName ->
                        entityManager
                                .createNativeQuery(String.format("TRUNCATE TABLE %s", tableName))
                                .executeUpdate());
        // PK 초기화
        tableNames.forEach(
                tableName ->
                        entityManager
                                .createNativeQuery(
                                        String.format(
                                                "ALTER TABLE %s ALTER COLUMN %s RESTART WITH 1",
                                                tableName, tableName.toLowerCase() + "_id"))
                                .executeUpdate());
        // 제약조건 무효화 해제
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}
