package com.optimagrowth.license.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Getter
@Setter
@ToString
@Entity
@Table(name = "license")
public class License extends RepresentationModel<License> {

  @Id
  @Column(name = "license_id", nullable = false)
  private String licenseId;

  private String description;

  @Column(name = "organization_id", nullable = false)
  private String organizationId;

  @Column(name = "product_name", nullable = false)
  private String productName;

  @Column(name = "license_type", nullable = false)
  private String licenseType;

  @Column(name = "comment")
  private String comment;

  @Transient
  private String organizationName;

  @Transient
  private String contactName;

  @Transient
  private String contactEmail;

  @Transient
  private String contactPhone;

  public License withComment(final String comment) {
    this.setComment(comment);
    return this;
  }

}
