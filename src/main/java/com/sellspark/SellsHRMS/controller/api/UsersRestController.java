
package com.sellspark.SellsHRMS.controller.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sellspark.SellsHRMS.dto.common.ApiResponse;
import com.sellspark.SellsHRMS.dto.users.ChanagePassworrdRequest;
import com.sellspark.SellsHRMS.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersRestController {
    private final UserService userService;

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@RequestBody ChanagePassworrdRequest request) {
        userService.changePassword(request.getEmail(), request.getCurrentPassword(), request.getNewPassword());

        return ResponseEntity.ok(ApiResponse.ok("Password changed successfully"));
    }

}