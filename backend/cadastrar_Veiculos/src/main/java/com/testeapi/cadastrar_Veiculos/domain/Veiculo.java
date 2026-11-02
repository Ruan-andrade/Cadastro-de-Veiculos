import com.testeapi.cadastrar_Veiculos.domain.Marca;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_veiculos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String veiculo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Marca marca;

    @Column(nullable = false)
    private Integer ano;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private Boolean vendido;

    @Setter(AccessLevel.NONE) // Bloqueia o setCreated para ninguém alterar a data de criação manualmente
    @Column(nullable = false, updatable = false)
    private LocalDateTime created;

    @Setter(AccessLevel.NONE) // Atualizado apenas via @PreUpdate
    @Column(nullable = false)
    private LocalDateTime updated;

    @PrePersist
    public void prePersist() {
        LocalDateTime agora = LocalDateTime.now();
        this.created = agora;
        this.updated = agora;
        if (this.vendido == null) {
            this.vendido = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updated = LocalDateTime.now();
    }
}