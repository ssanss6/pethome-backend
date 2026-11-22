package com.pethome.controller;

import com.pethome.common.result.Result;
import com.pethome.dto.PetDTO;
import com.pethome.entity.Pet;
import com.pethome.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
 * 宠物档案接口（宠物主，数据范围自动限定本人）
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "宠物档案")
@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @GetMapping
    public Result<List<Pet>> list() {
        return Result.ok(petService.listMine());
    }

    @GetMapping("/{id}")
    public Result<Pet> get(@PathVariable Long id) {
        return Result.ok(petService.getMine(id));
    }

    @PostMapping
    public Result<Pet> create(@Valid @RequestBody PetDTO dto) {
        return Result.ok(petService.create(dto));
    }

    @PutMapping("/{id}")
    public Result<Pet> update(@PathVariable Long id, @Valid @RequestBody PetDTO dto) {
        return Result.ok(petService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        petService.delete(id);
        return Result.ok();
    }
}
