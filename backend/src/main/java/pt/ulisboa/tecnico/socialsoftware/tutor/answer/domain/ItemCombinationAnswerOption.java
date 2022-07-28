package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class ItemCombinationAnswerOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private ItemCombinationAnswer itemCombinationAnswer;

    @ManyToOne(optional = false)
    private ItemCombinationOption itemCombinationOption;

    @ElementCollection
    private List<Integer> correspondences = new ArrayList<>();

    private String side;


    public ItemCombinationAnswerOption() {
    }

    public ItemCombinationAnswerOption(ItemCombinationOption itemCombinationOption, ItemCombinationAnswer itemCombinationAnswer, List<Integer> correspondences) {
        setItemCombinationOption(itemCombinationOption);
        setItemCombinationAnswer(itemCombinationAnswer);
        setCorrespondences(correspondences);
        this.side = itemCombinationOption.getSide().name();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ItemCombinationAnswer getItemCombinationAnswer() {
        return itemCombinationAnswer;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public void setItemCombinationAnswer(ItemCombinationAnswer itemCombinationAnswer) {
        this.itemCombinationAnswer = itemCombinationAnswer;
    }

    public ItemCombinationOption getItemCombinationOption() {
        return itemCombinationOption;
    }

    public void setItemCombinationOption(ItemCombinationOption itemCombinationOption) {
        this.itemCombinationOption = itemCombinationOption;
    }

    public List<Integer> getCorrespondences() {
        return correspondences;
    }

    public void setCorrespondences(List<Integer> correctCorrespondences) {
        this.correspondences = correctCorrespondences;
    }

    public void remove() {
        this.itemCombinationOption.getAnswerOptions().remove(this);
        this.itemCombinationOption = null;
    }

    public boolean isCorrect() {
        List<Integer> answeredList = this.itemCombinationOption.correspondencesToIntegerList();
        Collections.sort(answeredList);
        Collections.sort(this.correspondences);
        return answeredList.equals(this.correspondences);
    }
}
