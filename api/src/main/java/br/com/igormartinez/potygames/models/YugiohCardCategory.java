package br.com.igormartinez.potygames.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "yugioh_card_categories")
public class YugiohCardCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "sub_category", nullable = false)
    private String subCategory;

    @Column(name = "main_deck", nullable = false)
    private Boolean mainDeck;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
    @JsonIgnore
    private List<YugiohCard> yugiohCards;

    public YugiohCardCategory() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public Boolean getMainDeck() {
        return mainDeck;
    }

    public void setMainDeck(Boolean mainDeck) {
        this.mainDeck = mainDeck;
    }

    public List<YugiohCard> getYugiohCards() {
        return yugiohCards;
    }

    public void setYugiohCards(List<YugiohCard> yugiohCards) {
        this.yugiohCards = yugiohCards;
    }

    @JsonIgnore
    public boolean isMonster() {
        return category.equals("Monster");
    }

    @JsonIgnore
    public boolean isLinkMonster() {
        return category.equals("Monster") && subCategory.contains("Link");
    }

    @JsonIgnore
    public boolean isPendulumMonster() {
        return category.equals("Monster") && subCategory.contains("Pendulum"); 
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((subCategory == null) ? 0 : subCategory.hashCode());
        result = prime * result + ((mainDeck == null) ? 0 : mainDeck.hashCode());
        result = prime * result + ((yugiohCards == null) ? 0 : yugiohCards.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        YugiohCardCategory other = (YugiohCardCategory) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (category == null) {
            if (other.category != null)
                return false;
        } else if (!category.equals(other.category))
            return false;
        if (subCategory == null) {
            if (other.subCategory != null)
                return false;
        } else if (!subCategory.equals(other.subCategory))
            return false;
        if (mainDeck == null) {
            if (other.mainDeck != null)
                return false;
        } else if (!mainDeck.equals(other.mainDeck))
            return false;
        if (yugiohCards == null) {
            if (other.yugiohCards != null)
                return false;
        } else if (!yugiohCards.equals(other.yugiohCards))
            return false;
        return true;
    }
    
}
