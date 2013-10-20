package org.modelmapper;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.spi.MappingContext;

public class ComplexConversion {
  public static class PaymentInfo {
    int id;
    String type;

    PaymentInfo(int id, String type) {
      this.id = id;
      this.type = type;
    }
  }

  public static class Customer {
    List<PaymentInfo> info;

    List<PaymentInfo> getInfo() {
      return info;
    }
  }

  public static class BillingInfoDTO {
    int id;
  }

  public static class ShippingInfoDTO {
    int id;
  }

  public static class CustomerDTO {
    List<BillingInfoDTO> billingInfo;
    List<ShippingInfoDTO> shippingInfo;

    void setBillingInfo(List<BillingInfoDTO> billingInfo) {
      this.billingInfo = billingInfo;
    }

    void setShippingInfo(List<ShippingInfoDTO> shippingInfo) {
      this.shippingInfo = shippingInfo;
    }
  }

  static class PaymentInfoConverter implements Converter<List<PaymentInfo>, List<Object>> {
    private boolean billing;

    PaymentInfoConverter(boolean billing) {
      this.billing = billing;
    }

    public List<Object> convert(MappingContext<List<PaymentInfo>, List<Object>> context) {
      List<PaymentInfo> payments = new ArrayList<PaymentInfo>();
      for (PaymentInfo p : context.getSource())
        if (!billing ^ "Billing".equals(p.type))
          payments.add(p);
      return context.getMappingEngine().map(context.create(payments, context.getDestinationType()));
    }
  }

  public static void main(String... args) throws Exception {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration()
        .setFieldMatchingEnabled(true)
        .setFieldAccessLevel(AccessLevel.PACKAGE_PRIVATE);

    modelMapper.addMappings(new PropertyMap<Customer, CustomerDTO>() {
      @Override
      protected void configure() {
        using(new PaymentInfoConverter(true)).map(source.getInfo()).setBillingInfo(null);
        using(new PaymentInfoConverter(false)).map(source.getInfo()).setShippingInfo(null);
      }
    });

    PaymentInfo info1 = new PaymentInfo(1, "Billing");
    PaymentInfo info2 = new PaymentInfo(2, "Shipping");
    PaymentInfo info3 = new PaymentInfo(3, "Billing");
    PaymentInfo info4 = new PaymentInfo(4, "Shipping");
    Customer customer = new Customer();
    customer.info = Arrays.asList(info1, info2, info3, info4);
    CustomerDTO dto = modelMapper.map(customer, CustomerDTO.class);

    assertEquals(dto.billingInfo.get(0).id, 1);
    assertEquals(dto.billingInfo.get(1).id, 3);
    assertEquals(dto.shippingInfo.get(0).id, 2);
    assertEquals(dto.shippingInfo.get(1).id, 4);
  }
}
