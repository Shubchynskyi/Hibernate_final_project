package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import util.Constants;

@Entity
@Table(name = Constants.CITY)
@Getter
@Setter
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToOne
    @JoinColumn(name = Constants.COUNTRY_ID)
    private Country country;
    private String district;
    private Integer population;

}
