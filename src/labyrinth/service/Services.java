package labyrinth.service;

import labyrinth.service.dialog.DialogService;
import labyrinth.service.persistence.SqlPersistenceService;
import labyrinth.service.serialization.SerializationService;

public class Services {

    private final SerializationService serializationService;
    private final DialogService dialogService;
    private final SqlPersistenceService persistenceService;

    public Services(SerializationService serializationService, DialogService dialogService, SqlPersistenceService persistenceService) {
        this.serializationService = serializationService;
        this.dialogService = dialogService;
        this.persistenceService = persistenceService;
    }

    public SerializationService getSerializationService() {
        return serializationService;
    }

    public DialogService getDialogService() {
        return dialogService;
    }

    public SqlPersistenceService getPersistenceService() {
        return persistenceService;
    }
}