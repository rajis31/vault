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

import java.util.ArrayList;
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
    private UserEntity user;

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public static List<KvEntity> findDecryptedById(int id, String secretKey){
        KvEntity kv = findById(id);
        List<KvEntity> kvList = new ArrayList<>();
        kvList.add(kv);

        for(KvEntity kvl: kvList){
            kvl.value = AES256.decrypt(kvl.value, secretKey);
        }
        return kvList;
    }

    public static List<KvEntity> findDecrypted(String secretKey){
          List<KvEntity> kvlist = listAll();
          
          for(KvEntity kvItem: kvlist){
              kvItem.value = AES256.decrypt(kvItem.value, secretKey);
          }
          return kvlist;
    }
}
