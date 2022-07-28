package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationOption;

import java.io.Serializable;

public class ItemCombinationOptionStatementQuestionDetailsDto implements Serializable {
    private Integer id;
    private String content;
    private Integer sequence;
    private String side;

    public ItemCombinationOptionStatementQuestionDetailsDto(ItemCombinationOption itemCombinationOption) {
        id = itemCombinationOption.getId();
        content = itemCombinationOption.getContent();
        sequence = itemCombinationOption.getSequence();
        side = itemCombinationOption.getSide().name();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }
}
