package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationOption;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ItemCombinationOptionDto implements Serializable {
    private Integer id;
    private String content;
    private Integer sequence;
    private String side;


    private List<Integer> correspondences = new ArrayList<>();

    public ItemCombinationOptionDto() {
    }

    public ItemCombinationOptionDto(ItemCombinationOption option){
        this.id = option.getId();
        this.content = option.getContent();
        this.sequence = option.getSequence();
        this.side = option.getSide().name();
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

    public List<Integer> getCorrespondences() {
        return correspondences;
    }

    public void setCorrespondences(List<Integer> correspondences){
        this.correspondences = correspondences;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    @Override
    public String toString() {
        return "ItemCombinationOption{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", sequence=" + sequence +
                ", Side=" + this.side +
                '}';
    }
}
