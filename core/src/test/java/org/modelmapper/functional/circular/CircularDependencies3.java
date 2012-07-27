package org.modelmapper.functional.circular;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.AbstractTest;
import org.modelmapper.MappingException;
import org.testng.annotations.Test;

/**
 * Tests many to many and circular scenarios.
 * 
 * Adapted from an AutoMapper test case.
 */
@Test(groups = "functional")
public class CircularDependencies3 extends AbstractTest {
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

  @Test(expectedExceptions = MappingException.class)
  public void shouldThrowOnMap() {
    SourceTown sourceTown = new SourceTown();
    SourceStreet sourceStreet = new SourceStreet();
    sourceStreet.town = sourceTown;
    sourceTown.streets.add(sourceStreet);

    SourceHouse sourceHouse = new SourceHouse();
    sourceHouse.street = sourceStreet;
    sourceStreet.houses.add(sourceHouse);
    SourceHouse sourceHouse2 = new SourceHouse();
    sourceHouse2.street = sourceStreet;
    sourceStreet.houses.add(sourceHouse2);

    modelMapper.map(sourceHouse, DestinationHouse.class);
  }

  // public void shouldMapStreets() {
  // SourceTown sourceTown = new SourceTown();
  // SourceStreet sourceStreet = new SourceStreet();
  // sourceStreet.town = sourceTown;
  // sourceTown.streets.add(sourceStreet);
  //
  // SourceHouse sourceHouse = new SourceHouse();
  // sourceHouse.street = sourceStreet;
  // sourceStreet.houses.add(sourceHouse);
  // SourceHouse sourceHouse2 = new SourceHouse();
  // sourceHouse2.street = sourceStreet;
  // sourceStreet.houses.add(sourceHouse2);
  //
  // DestinationHouse targetHouse = modelMapper.map(sourceHouse, DestinationHouse.class);
  // DestinationStreet targetStreet = targetHouse.street;
  // assertEquals(targetStreet.houses.get(0).street, targetStreet);
  // assertEquals(targetStreet.houses.get(1).street, targetStreet);
  // }
  //
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
