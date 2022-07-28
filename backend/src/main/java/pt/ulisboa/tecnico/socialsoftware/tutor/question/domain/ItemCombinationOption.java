package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.ItemCombinationAnswerOption;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationOptionDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "item_combination_options")
public class ItemCombinationOption implements DomainEntity {
    public enum SIDE {
        LEFT, RIGHT
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private SIDE side;

    @Column(nullable = false)
    private Integer sequence;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_details_id")
    private ItemCombinationQuestion questionDetails;

    @OneToMany(mappedBy = "itemCombinationOption", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ItemCombinationAnswerOption> answerOptions = new ArrayList<>();

    @ManyToMany()
    private List<ItemCombinationOption> correspondences = new ArrayList<>();

    public ItemCombinationOption(){

    }

    public ItemCombinationOption(ItemCombinationOptionDto dto){
        setContent(dto.getContent());
        setSequence(dto.getSequence());
        setSide(SIDE.valueOf(dto.getSide()));
    }

    public void setSide(SIDE side){
        this.side = side;
    }

    public SIDE getSide(){
        return this.side;
    }

    public Integer getId() {
        return id;
    }

    public List<ItemCombinationOption> getCorrespondences() {
        return this.correspondences;
    }

    public void setCorrespondences(List<ItemCombinationOption> correspondences) {
        this.correspondences = correspondences;
    }

    public void addCorrespondence(ItemCombinationOption option){
        this.correspondences.add(option);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public List<ItemCombinationAnswerOption> getAnswerOptions() {
        return answerOptions;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ItemCombinationQuestion getQuestionDetails() {
        return questionDetails;
    }

    public void setQuestionDetails(ItemCombinationQuestion questionDetails){
        this.questionDetails = questionDetails;
        questionDetails.addOption(this);
    }

    public void remove(){
        this.questionDetails = null;
        for (ItemCombinationOption option : this.correspondences){
            option.removeCorrespondence(this);
        }
        this.correspondences.clear();
    }

    public void removeCorrespondence(ItemCombinationOption option){
        this.correspondences.remove(option);
    }

    public List<Integer> correspondencesToIntegerList(){
        List<Integer> list = new ArrayList<>();
        for (ItemCombinationOption option : this.correspondences){
            list.add(option.getId());
        }
        return list;
    }



    @Override
    public void accept(Visitor visitor) {
        visitor.visitItemCombinationOption(this);
    }
}
