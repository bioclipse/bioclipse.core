package org.springframework.osgi.iandt.annotation.dependencies;

import java.util.SortedSet;

import org.springframework.osgi.iandt.simpleservice2.MyService2;
import org.springframework.osgi.iandt.simpleservice.MyService;
import org.springframework.osgi.extensions.annotation.ServiceReference;

/**
 * @author Andy Piper
 * @author Hal Hildebrand
 */
public class DependentImpl implements Dependent {
    private MyService service1;
    private MyService2 service2;
    private MyService2 service3;
	private SortedSet<MyService> servicecollection;


    @ServiceReference
    public void setService1(MyService service1) {
        this.service1 = service1;
    }

    public void setService2(MyService2 service2) {
        this.service2 = service2;
    }

	@ServiceReference
	public void setServiceCollection(SortedSet<MyService> service1) {
	    this.servicecollection = service1;
	}

    @ServiceReference
    public void setService3(MyService2 service3) {
        this.service3 = service3;
    }


    public boolean isResolved() {
        return service2 != null && service3 != null && service1 != null && servicecollection != null
	        && servicecollection.size() > 0;
    }
}
