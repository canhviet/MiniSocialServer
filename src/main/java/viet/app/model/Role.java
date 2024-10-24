package viet.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_role")
public class Role extends AbstractEntity<Integer> {

    @Column(name = "name")
    private String name;

    @OneToMany
    private Set<RoleHasPermission> permissions = new HashSet<>();

    public List<String> getPermissionNames() {
        return permissions.stream()
                .map(roleHasPermission -> roleHasPermission.getPermission().getName())
                .collect(Collectors.toList());
    }
}
