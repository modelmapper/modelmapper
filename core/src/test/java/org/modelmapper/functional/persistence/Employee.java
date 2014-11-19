package org.modelmapper.functional.persistence;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Employee {
  @GeneratedValue @Id public long id;
}