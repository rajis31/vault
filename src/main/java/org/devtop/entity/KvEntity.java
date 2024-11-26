/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.devtop.entity;

/**
 *
 * @author raji
 */
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.util.List;

import org.devtop.encryption.AES256;

@Entity
@Table(name = "kv")
public class KvEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String key;
    public String value;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static List<KvEntity> findDecrypted(String secretKey){
          List<KvEntity> kvlist = listAll();
          
          for(KvEntity kv: kvlist){
              kv.value = AES256.decrypt(kv.value, secretKey);
          }
          return kvlist;
    }
}
