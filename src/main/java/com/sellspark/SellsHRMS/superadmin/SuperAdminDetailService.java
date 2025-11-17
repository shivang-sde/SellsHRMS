package com.sellspark.SellsHRMS.superadmin;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SuperAdminDetailService implements UserDetailsService {
  
  
    
    private final SuperAdminService superAdminService;


    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        SuperAdmin admin = superAdminService.findByEmail(email);

        if (admin == null) {
            throw new UsernameNotFoundException("SUper Admin not found");
        }

        return new SuperAdminDetails(admin);
    }

    public UserDetails loadUserByUseremail(String email) throws UsernameNotFoundException{
        return new SuperAdminDetails(superAdminService.findByEmail(email));
    }

}
