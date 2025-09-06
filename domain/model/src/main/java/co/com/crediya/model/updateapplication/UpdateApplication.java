package co.com.crediya.model.updateapplication;

import co.com.crediya.model.states.StatesEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateApplication {
    private Integer id;
    private StatesEnum state;
}
