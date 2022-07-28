package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ItemCombinationOptionStatementAnswerDetailsDto;

import javax.persistence.*;
import java.util.List;

@Entity
public class ItemCombinationOptionAnswerItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer optionId;

    @ElementCollection
    private List<Integer> correspondences;

    public ItemCombinationOptionAnswerItem() {}

    public ItemCombinationOptionAnswerItem(ItemCombinationOptionStatementAnswerDetailsDto optionDto) {
        optionId = optionDto.getOptionId();
        correspondences = optionDto.getCorrespondences();
    }

    public Integer getOptionId() {
        return optionId;
    }

    public void setOptionId(Integer slotId) {
        this.optionId = slotId;
    }

    public List<Integer> getCorrespondences() {
        return correspondences;
    }

    public void setCorrespondences(List<Integer> correspondences) {
        this.correspondences = correspondences;
    }
}
