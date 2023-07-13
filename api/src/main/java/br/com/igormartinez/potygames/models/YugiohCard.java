package br.com.igormartinez.potygames.models;

import java.util.List;

import br.com.igormartinez.potygames.enums.YugiohCardAttribute;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "yugioh_cards")
public class YugiohCard {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_ygoprodeck")
    private Long idYgoprodeck;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "category", nullable = false)
    private YugiohCardCategory category; 

    @ManyToOne
    @JoinColumn(name = "type")
    private YugiohCardType type;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "attribute")
    private YugiohCardAttribute attribute;
    
    @Column(name = "level_rank_link")
    private Integer levelRankLink;

    @Column(name = "effect_lore_text")
    private String effectLoreText;

    @Column(name = "pendulum_scale")
    private Integer pendulumScale;

    @Column(name = "link_arrows")
    private List<String> linkArrows;

    @Column(name = "atk")
    private Integer atk;

    @Column(name = "def")
    private Integer def;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "yugiohCard")
    private List<InventoryItem> inventoryItems;

    public YugiohCard() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdYgoprodeck() {
        return idYgoprodeck;
    }

    public void setIdYgoprodeck(Long idYgoprodeck) {
        this.idYgoprodeck = idYgoprodeck;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public YugiohCardCategory getCategory() {
        return category;
    }

    public void setCategory(YugiohCardCategory category) {
        this.category = category;
    }

    public YugiohCardType getType() {
        return type;
    }

    public void setType(YugiohCardType type) {
        this.type = type;
    }

    public YugiohCardAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(YugiohCardAttribute attribute) {
        this.attribute = attribute;
    }

    public Integer getLevelRankLink() {
        return levelRankLink;
    }

    public void setLevelRankLink(Integer levelRankLink) {
        this.levelRankLink = levelRankLink;
    }

    public String getEffectLoreText() {
        return effectLoreText;
    }

    public void setEffectLoreText(String effectLoreText) {
        this.effectLoreText = effectLoreText;
    }

    public Integer getPendulumScale() {
        return pendulumScale;
    }

    public void setPendulumScale(Integer pendulumScale) {
        this.pendulumScale = pendulumScale;
    }

    public List<String> getLinkArrows() {
        return linkArrows;
    }

    public void setLinkArrows(List<String> linkArrows) {
        this.linkArrows = linkArrows;
    }

    public Integer getAtk() {
        return atk;
    }

    public void setAtk(Integer atk) {
        this.atk = atk;
    }

    public Integer getDef() {
        return def;
    }

    public void setDef(Integer def) {
        this.def = def;
    }

    public List<InventoryItem> getInventoryItems() {
        return inventoryItems;
    }

    public void setInventoryItems(List<InventoryItem> inventoryItems) {
        this.inventoryItems = inventoryItems;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((idYgoprodeck == null) ? 0 : idYgoprodeck.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
        result = prime * result + ((levelRankLink == null) ? 0 : levelRankLink.hashCode());
        result = prime * result + ((effectLoreText == null) ? 0 : effectLoreText.hashCode());
        result = prime * result + ((pendulumScale == null) ? 0 : pendulumScale.hashCode());
        result = prime * result + ((linkArrows == null) ? 0 : linkArrows.hashCode());
        result = prime * result + ((atk == null) ? 0 : atk.hashCode());
        result = prime * result + ((def == null) ? 0 : def.hashCode());
        result = prime * result + ((inventoryItems == null) ? 0 : inventoryItems.hashCode());
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
        YugiohCard other = (YugiohCard) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (idYgoprodeck == null) {
            if (other.idYgoprodeck != null)
                return false;
        } else if (!idYgoprodeck.equals(other.idYgoprodeck))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (category == null) {
            if (other.category != null)
                return false;
        } else if (!category.equals(other.category))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (attribute != other.attribute)
            return false;
        if (levelRankLink == null) {
            if (other.levelRankLink != null)
                return false;
        } else if (!levelRankLink.equals(other.levelRankLink))
            return false;
        if (effectLoreText == null) {
            if (other.effectLoreText != null)
                return false;
        } else if (!effectLoreText.equals(other.effectLoreText))
            return false;
        if (pendulumScale == null) {
            if (other.pendulumScale != null)
                return false;
        } else if (!pendulumScale.equals(other.pendulumScale))
            return false;
        if (linkArrows == null) {
            if (other.linkArrows != null)
                return false;
        } else if (!linkArrows.equals(other.linkArrows))
            return false;
        if (atk == null) {
            if (other.atk != null)
                return false;
        } else if (!atk.equals(other.atk))
            return false;
        if (def == null) {
            if (other.def != null)
                return false;
        } else if (!def.equals(other.def))
            return false;
        if (inventoryItems == null) {
            if (other.inventoryItems != null)
                return false;
        } else if (!inventoryItems.equals(other.inventoryItems))
            return false;
        return true;
    }
}
