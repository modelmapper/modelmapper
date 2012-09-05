package org.modelmapper.functional.circular;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * Tests many to many and circular scenarios.
 * 
 * Adapted from an AutoMapper test case.
 */
@Test(groups = "functional")
public class CircularDependencies5 extends AbstractTest {
  static class SourceTown {
    List<SourceStreet> streets = new ArrayList<SourceStreet>();
  }

  static class SourceStreet {
    List<SourceHouse> houses = new ArrayList<SourceHouse>();
    SourceTown town;
  }

  static class SourceHouse {
    SourceStreet street;
  }

  static class DestinationTown {
    List<DestinationStreet> streets = new ArrayList<DestinationStreet>();
  }

  static class DestinationStreet {
    List<DestinationHouse> houses = new ArrayList<DestinationHouse>();
    DestinationTown town;
  }

  static class DestinationHouse {
    DestinationStreet street;
  }
  
  public void shouldMapTowns() {
    SourceTown sourceTown = new SourceTown();
    SourceStreet sourceStreet = new SourceStreet();
    sourceStreet.town = sourceTown;
    sourceTown.streets.add(sourceStreet);

    SourceHouse sourceHouse1 = new SourceHouse();
    sourceHouse1.street = sourceStreet;
    sourceStreet.houses.add(sourceHouse1);
    SourceHouse sourceHouse2 = new SourceHouse();
    sourceHouse2.street = sourceStreet;
    sourceStreet.houses.add(sourceHouse2);

    DestinationTown destTown = modelMapper.map(sourceTown, DestinationTown.class);
    
    DestinationStreet street = destTown.streets.get(0);
    List<DestinationHouse> houses = street.houses;
    assertEquals(street.town, destTown);
    assertEquals(houses.get(0).street, street);
    assertEquals(houses.get(1).street, street);
  }

  public void shouldMapHouses() {
    SourceTown sourceTown = new SourceTown();
    SourceStreet sourceStreet = new SourceStreet();
    sourceStreet.town = sourceTown;
    sourceTown.streets.add(sourceStreet);

    SourceHouse sourceHouse1 = new SourceHouse();
    sourceHouse1.street = sourceStreet;
    sourceStreet.houses.add(sourceHouse1);
    SourceHouse sourceHouse2 = new SourceHouse();
    sourceHouse2.street = sourceStreet;
    sourceStreet.houses.add(sourceHouse2);

    DestinationHouse destHouse1 = modelMapper.map(sourceHouse1, DestinationHouse.class);
    
    DestinationStreet destStreet = destHouse1.street;
    assertEquals(destStreet.town.streets.get(0), destStreet);
    assertEquals(destStreet.houses.get(0).street, destStreet);
    assertEquals(destStreet.houses.get(1).street, destStreet);
  }
  
  public void shouldMapStreet() {
    SourceTown sourceTown = new SourceTown();
    SourceStreet sourceStreet = new SourceStreet();
    sourceStreet.town = sourceTown;
    sourceTown.streets.add(sourceStreet);

    SourceHouse sourceHouse1 = new SourceHouse();
    sourceHouse1.street = sourceStreet;
    sourceStreet.houses.add(sourceHouse1);
    SourceHouse sourceHouse2 = new SourceHouse();
    sourceHouse2.street = sourceStreet;
    sourceStreet.houses.add(sourceHouse2);

    DestinationStreet destStreet = modelMapper.map(sourceStreet, DestinationStreet.class);
    
    assertEquals(destStreet.town.streets.get(0), destStreet);
    assertEquals(destStreet.houses.get(0).street, destStreet);
    assertEquals(destStreet.houses.get(1).street, destStreet);
  }

  // [Test]
  // public void MapsTownStreetRelationshipCorrectlyWhenOneHouseAndOneStreetAndOneTown()
  // {
  // var sourceTown = new Source.Town();
  //
  // var sourceStreet = new Source.Street { Town = sourceTown };
  // sourceTown.Streets.Add(sourceStreet);
  //
  // var sourceHouse = new Source.House { Street = sourceStreet };
  // sourceStreet.Houses.Add(sourceHouse);
  //
  // var targetHouse = AutoMapper.Mapper.Map<Source.House, Target.House>(sourceHouse);
  //
  // Assert.That(targetHouse.Street.Town.Streets[0], Is.EqualTo(targetHouse.Street));
  // }
  //
  // [Test]
  // public void MapsTownStreetRelationshipCorrectlyWhenMultipleStreetsInTown()
  // {
  // var sourceTown = new Source.Town();
  //
  // var sourceStreet = new Source.Street { Town = sourceTown};
  // sourceTown.Streets.Add(sourceStreet);
  //
  // var sourceStreet2 = new Source.Street { Town = sourceTown};
  // sourceTown.Streets.Add(sourceStreet2);
  //
  // var sourceHouse = new Source.House {Street = sourceStreet2};
  // sourceStreet2.Houses.Add(sourceHouse);
  //
  // var targetHouse = AutoMapper.Mapper.Map<Source.House, Target.House>(sourceHouse);
  //
  // Assert.That(targetHouse.Street.Town.Streets[1], Is.EqualTo(targetHouse.Street));
  // }
}
