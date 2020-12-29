/*
 * Copyright (c) 2016. Jan Wiemer
 */

package org.jacisclient;

import org.jacis.container.JacisContainer;
import org.jacis.container.JacisObjectTypeSpec;
import org.jacis.extension.persistence.MicrostreamPersistenceAdapter;
import org.jacis.plugin.objectadapter.cloning.JacisCloningObjectAdapter;
import org.jacis.store.JacisStore;
import org.jacisclient.JacisExample1GettingStarted.Account;

import one.microstream.storage.configuration.Configuration;
import one.microstream.storage.types.EmbeddedStorageManager;

/**
 * Example 6: MicroStream Persistance Adapter.
 *
 * @author Jan Wiemer
 */
public class JacisExample6PersistenceAdapterMicroStream {

  // Note that we use the same account object introduced for the first example

  public static void main(String[] args) {
    { // first start a container and a store with persistence
      JacisContainer container = new JacisContainer();
      JacisObjectTypeSpec<String, Account, Account> objectTypeSpec //
          = new JacisObjectTypeSpec<>(String.class, Account.class, new JacisCloningObjectAdapter<>());
      // start a MicroStream storage manager
      EmbeddedStorageManager storageManager = Configuration.Default() //
          .createEmbeddedStorageFoundation() //
          .createEmbeddedStorageManager();
      // setBase.createEmbeddedStorageManager();
      // set the persistence adapter extension using the storage manager
      objectTypeSpec.setPersistenceAdapter(new MicrostreamPersistenceAdapter<>(storageManager));
      JacisStore<String, Account> store = container.createStore(objectTypeSpec).getStore();
      // create some objects
      container.withLocalTx(() -> {
        store.update("account1", new Account("account1").deposit(-100));
        store.update("account2", new Account("account2").deposit(10));
        store.update("account3", new Account("account3").deposit(100));
      });
      storageManager.close();
    }
    { // simulate restart and start a new container and a new store
      JacisContainer container = new JacisContainer();
      JacisObjectTypeSpec<String, Account, Account> objectTypeSpec //
          = new JacisObjectTypeSpec<>(String.class, Account.class, new JacisCloningObjectAdapter<>());
      // start a MicroStream storage manager
      EmbeddedStorageManager storageManager = Configuration.Default() //
          .createEmbeddedStorageFoundation() //
          .createEmbeddedStorageManager();
      // set the persistence adapter extension using the storage manager
      objectTypeSpec.setPersistenceAdapter(new MicrostreamPersistenceAdapter<>(storageManager));
      JacisStore<String, Account> store = container.createStore(objectTypeSpec).getStore();
      // check the objects are still in the store
      container.withLocalTx(() -> {
        store.stream().forEach(acc -> System.out.println("balance(" + acc.getName() + ")= " + acc.getBalance()));
      });
      storageManager.close();
    }
    System.exit(1);
  }
}