package ir.alimojahed.general.elasticwrapper.data.hibernate.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author Ali Mojahed on 10/5/2022
 * @project iso3
 **/

@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class JpaBaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private long id;

    @Column(updatable = false, name = "INSERT_TIME")
    private LocalDateTime insertTime;

    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        insertTime = updateTime = LocalDateTime.now();

    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JpaBaseEntity that = (JpaBaseEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
