package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.ItemCombinationAnswerOption;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ItemCombinationOptionStatementAnswerDetailsDto implements Serializable {
    private Integer optionId;

    private String side;

    private Integer sequence;

    private List<Integer> correspondences;

    public ItemCombinationOptionStatementAnswerDetailsDto() {
    }

    public ItemCombinationOptionStatementAnswerDetailsDto(Integer optionId, String side, List<Integer> correspondences) {
        this.optionId = optionId;
        this.side = side;
        this.correspondences = correspondences;
    }

    public ItemCombinationOptionStatementAnswerDetailsDto(ItemCombinationAnswerOption option) {

        this.correspondences = new ArrayList<>(option.getCorrespondences());
        this.side = option.getItemCombinationOption().getSide().name();
        this.sequence = option.getItemCombinationOption().getSequence();
        this.optionId = option.getItemCombinationOption().getId();
    }

    public Integer getOptionId() {
        return optionId;
    }

    public void setOptionId(Integer optionId) {
        this.optionId = optionId;
    }

    public List<Integer> getCorrespondences() {
        return correspondences;
    }

    public void setCorrespondences(List<Integer> correspondences) {
        this.correspondences = correspondences;
    }
}