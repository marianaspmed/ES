package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.ItemCombinationAnswerOption;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationOption;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ItemCombinationAnswerOptionDto implements Serializable {
    private Integer id;
    private String content;
    private String side;
    private Integer sequence;

    private List<Integer> correspondences = new ArrayList<>();

    public ItemCombinationAnswerOptionDto(ItemCombinationOption correctOption) {
        this.id = correctOption.getId();
        this.content = correctOption.getContent();
        this.sequence = correctOption.getSequence();
        this.side = correctOption.getSide().name();
    }

    public ItemCombinationAnswerOptionDto(ItemCombinationAnswerOption answerOption) {
        this.id = answerOption.getItemCombinationOption().getId();
        this.side = answerOption.getSide();
        this.sequence = answerOption.getItemCombinationOption().getSequence();
        this.correspondences = new ArrayList<>(answerOption.getCorrespondences());
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

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public List<Integer> getCorrespondences() {
        return correspondences;
    }

    public void setCorrespondences(List<Integer> correspondences) {
        this.correspondences = correspondences;
    }
}
