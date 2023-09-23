package ru.nsu.fit.pak.budle;

import org.flywaydb.test.annotation.FlywayTest;
import org.flywaydb.test.dbunit.DBUnitSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.nsu.fit.pak.budle.exceptions.WorkerNotFoundException;
import ru.nsu.fit.pak.budle.service.WorkerService;

@DisplayName("Тест на бизнес-логику работников заведения.")
@FlywayTest
public class WorkerBusinessLogicTests extends AbstractContextualTest {
    private static final Long ESTABLISHMENT_ID = 100L;
    private static final Long WORKER_ID = 1L;

    private static final String WORKER_PHONE_NUMBER = "+79991232233";
    @Autowired
    private WorkerService workerService;

    @Test
    @DBUnitSupport(loadFilesForRun = {"CLEAN_INSERT", "/worker/before/establishment_with_worker.xml"})
    @DisplayName("Тест на получение работников заведения")
    public void testGetWorkers() {
        Assertions.assertEquals(
            1,
            workerService.getWorkers(ESTABLISHMENT_ID).size()
        );

    }

    @Test
    @DBUnitSupport(loadFilesForRun = {"CLEAN_INSERT", "/worker/before/establishment_with_worker.xml"})
    @DisplayName("Тест на удаление работника")
    public void testDeleteWorker() {
        workerService.deleteWorker(WORKER_ID, ESTABLISHMENT_ID);
        Assertions.assertTrue(workerService.getWorkers(ESTABLISHMENT_ID).isEmpty());
    }

    @Test
    @DBUnitSupport(loadFilesForRun = {"CLEAN_INSERT", "/worker/before/establishment_without_worker.xml"})
    @DisplayName("Тест на создание работника")
    public void testCreateWorker() {
        workerService.createWorker(WORKER_PHONE_NUMBER, ESTABLISHMENT_ID);
        Assertions.assertEquals(
            1,
            workerService.getWorkers(ESTABLISHMENT_ID).size()
        );

    }

    @Test
    @FlywayTest
    @DisplayName("Тест на получение несуществующего работника")
    @DBUnitSupport(loadFilesForRun = {"CLEAN_INSERT", "/worker/before/establishment_without_worker.xml"})
    public void getNonExistenceWorker() {
        Assertions.assertThrows(
            WorkerNotFoundException.class,
            () -> workerService.deleteWorker(123L, ESTABLISHMENT_ID)
        );
    }
}
