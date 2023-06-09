package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import util.Constants;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = Constants.COUNTRY)
@Getter
@Setter
public class Country {
    @Id
    @Column(nullable = false)
    private Integer id;
    private String code;
    private String code_2;
    private String name;
    @Column(name = Constants.CONTINENT)
    @Enumerated(EnumType.ORDINAL)
    private Continent continent;
    private String region;
    private BigDecimal surfaceArea;
    private Short indepYear;
    private Integer population;
    private BigDecimal lifeExpectancy;
    private BigDecimal gnp;
    private BigDecimal gnpoId;
    private String localName;
    private String governmentForm;
    private String headOfState;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.CAPITAL)
    private City capital;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = Constants.COUNTRY_ID)
    private Set<CountryLanguage> languages;


}
