package com.e.myrealmtest;

import androidx.appcompat.app.AppCompatActivity;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RealmResults<Dog> puppies;
    private Person person;
    private Realm realm;
    private Dog managedDog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use them like regular java objects
                Dog dog = new Dog();
                dog.setName("Rex");
                dog.setAge(1);

// Persist your data in a transaction
                realm.beginTransaction();
                managedDog = realm.copyToRealm(dog); // Persist unmanaged objects
                person = realm.createObject(Person.class); // Create managed objects directly
                //     Person person = realm.createObject(Person.class, UUID.randomUUID().toString());
                person.getDogs().add(managedDog);
                realm.commitTransaction();

            }
        });

        findViewById(R.id.read).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                person = realm.where(Person.class).findFirst();
            }
        });

        findViewById(R.id.write).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.beginTransaction();

                // Use them like regular java objects
                Dog dog = new Dog();
                dog.setName("Rex");
                dog.setAge(1);
                managedDog = realm.copyToRealm(dog); // Persist unmanaged objects

                person = realm.where(Person.class).findFirst();
                person.getDogs().add(managedDog);
                realm.commitTransaction();
            }
        });

        findViewById(R.id.init_realm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize Realm (just once per application)
                Realm.init(getApplicationContext());

// Get a Realm instance for this thread
                realm = Realm.getDefaultInstance();
            }
        });

        findViewById(R.id.puppies_example).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Query Realm for all dogs younger than 2 years old
                puppies = realm.where(Dog.class).lessThan("age", 2).findAll();
                puppies.size(); // => 0 because no dogs have been added to the Realm yet


                // Listeners will be notified when data changes
                puppies.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Dog>>() {
                    @Override
                    public void onChange(RealmResults<Dog> results, OrderedCollectionChangeSet changeSet) {
                        // Query results are updated in real time with fine grained notifications.
                        changeSet.getInsertions(); // => [0] is added.
                    }
                });
            }
        });

        findViewById(R.id.write_dogs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

// Persist your data in a transaction
                realm.beginTransaction();

                String name = "Rex";
                for (int i = 0; i < 6; i++) {
                    name = name + i;
                    Dog dog = new Dog();
                    dog.setName(name);
                    dog.setAge(1);

                    managedDog = realm.copyToRealm(dog); // Persist unmanaged objects
                }

                realm.commitTransaction();
            }
        });

        findViewById(R.id.read_dogs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RealmResults<Dog> dogs = realm.where(Dog.class).findAll();
            }
        });

        findViewById(R.id.delete_dog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmResults<Dog> result = realm.where(Dog.class).equalTo("name","Rex01").findAll();
                        result.deleteAllFromRealm();
                    }
                });
            }
        });

    }
}
