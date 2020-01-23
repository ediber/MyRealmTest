package com.e.myrealmtest;

import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;
import io.realm.RealmResults;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use them like regular java objects
                Dog dog = new Dog();
                dog.setName("Rex");
                dog.setAge(1);

// Initialize Realm (just once per application)
                Realm.init(getApplicationContext());

// Get a Realm instance for this thread
                Realm realm = Realm.getDefaultInstance();

// Query Realm for all dogs younger than 2 years old
                final RealmResults<Dog> puppies = realm.where(Dog.class).lessThan("age", 2).findAll();
                puppies.size(); // => 0 because no dogs have been added to the Realm yet

// Persist your data in a transaction
                realm.beginTransaction();
                final Dog managedDog = realm.copyToRealm(dog); // Persist unmanaged objects
                Person person = realm.createObject(Person.class); // Create managed objects directly
                person.getDogs().add(managedDog);
                realm.commitTransaction();
            }
        });


    }
}
