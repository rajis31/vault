/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.devtop.entity;

/**
 *
 * @author raji
 */
import jakarta.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.util.List;
import org.devtop.encryption.AES256;

@Entity
public class KvEntity extends PanacheEntity {
    public String key;
    public String value;
    
    public static List<KvEntity> findDecrypted(){
          List<KvEntity> kvlist = listAll();
          kvlist.forEach(kv -> AES256.  );
          
    }
}
