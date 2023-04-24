package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import util.Constants;

import java.math.BigDecimal;

@Entity
@Table(name = Constants.COUNTRY_LANGUAGE)
@Getter
@Setter
public class CountryLanguage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = Constants.COUNTRY_ID)
    private Country country;
    private String language;
    @Column(columnDefinition = "BIT")
    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    private Boolean isOfficial;
    private BigDecimal percentage;

}
