package com.pethome.controller;

import com.pethome.common.result.Result;
import com.pethome.dto.RoomTypeDTO;
import com.pethome.entity.RoomType;
import com.pethome.service.RoomTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 房型管理接口（后台）
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "房型管理")
@RestController
@RequestMapping("/api/admin/room-types")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('STORE_MANAGER','PLATFORM_ADMIN')")
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    @GetMapping
    public Result<List<RoomType>> list() {
        return Result.ok(roomTypeService.list());
    }

    @GetMapping("/{id}")
    public Result<RoomType> get(@PathVariable Long id) {
        return Result.ok(roomTypeService.get(id));
    }

    @PostMapping
    public Result<RoomType> create(@Valid @RequestBody RoomTypeDTO dto) {
        return Result.ok(roomTypeService.create(dto));
    }

    @PutMapping("/{id}")
    public Result<RoomType> update(@PathVariable Long id, @Valid @RequestBody RoomTypeDTO dto) {
        return Result.ok(roomTypeService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roomTypeService.delete(id);
        return Result.ok();
    }
}
