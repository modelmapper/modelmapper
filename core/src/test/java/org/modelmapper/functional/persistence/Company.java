package org.modelmapper.functional.persistence;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Company {
  @Id @GeneratedValue private long id;
  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST) List<Employee> employees;
}