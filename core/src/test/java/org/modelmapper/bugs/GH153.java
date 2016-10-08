package org.modelmapper.bugs;

import java.util.Date;

import org.modelmapper.AbstractTest;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * https://github.com/jhalterman/modelmapper/issues/153
 */
@Test
public class GH153 extends AbstractTest {
    class SkipAuditPersistMapping<S, T extends AuditPersist> extends PropertyMap<S, T>
    {
        @Override
        protected void configure() {
            skip().setCreateDate(null);
            skip().setCreatedBy(null);
            skip().setModifiedDate(null);
            skip().setModifiedBy(null);
        }
    }

    static class BoringClass {}

    interface AuditPersist {
        Date getCreateDate();
        void setCreateDate(Date createDate);
        String getCreatedBy();
        void setCreatedBy(String createdBy);
        Date getModifiedDate();
        void setModifiedDate(Date modifiedDate);
        String getModifiedBy();
        void setModifiedBy(String modifiedBy);
    }

    static class AuditedClass implements AuditPersist {
        private Date createDate;
        private String createdBy;
        private Date modifiedDate;
        private String modifiedBy;

        public Date getCreateDate() {
            return createDate;
        }

        public void setCreateDate(Date createDate) {
            this.createDate = createDate;
        }

        public String getCreatedBy() {
            return this.createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public Date getModifiedDate() {
            return modifiedDate;
        }

        public void setModifiedDate(Date modifiedDate) {
            this.modifiedDate = modifiedDate;
        }

        public String getModifiedBy() {
            return modifiedBy;
        }

        public void setModifiedBy(String modifiedBy) {
            this.modifiedBy = modifiedBy;
        }

    }

    @Test
    public void testMappingClassWithoutDi() {
        modelMapper.createTypeMap(BoringClass.class, AuditedClass.class).addMappings(new SkipAuditPersistMapping<BoringClass, AuditedClass>());
        AuditedClass auditedClass = modelMapper.map(new BoringClass(), AuditedClass.class);
        Assert.assertNotNull(auditedClass);
    }
}
