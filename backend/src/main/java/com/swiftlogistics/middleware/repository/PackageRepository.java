package com.swiftlogistics.middleware.repository;

import com.swiftlogistics.middleware.model.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {
    
    Package findByPackageId(String packageId);
    
    List<Package> findByOrderId(Long orderId);
    
    List<Package> findByStatus(Package.PackageStatus status);
}
