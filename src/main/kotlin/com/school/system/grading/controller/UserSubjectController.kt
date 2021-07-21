package com.school.system.grading.controller

import com.school.system.grading.controller.common.BaseController
import com.school.system.grading.datasource.UserSubjectDataSource
import com.school.system.grading.entity.*
import com.school.system.grading.entity.subject.request.ArchiveSubjectRequest
import com.school.system.grading.entity.subject.request.RemoveSubjectRequest
import com.school.system.grading.entity.subject.request.UserSubjectRequest
import com.school.system.grading.entity.subject.request.UserSubjectUpdateRequest
import com.school.system.grading.service.UserSubjectService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * @project grading
 * @author Muhammad Saad
 * Date: 6/8/21
 **/
@RestController
@RequestMapping("api/v1/subjects")
class UserSubjectController(
        private val userSubjectService: UserSubjectService
): BaseController() {

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun getAllSubjects() = userSubjectService.getAllSubjects()

    @GetMapping("/archived",produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun getAllArchivedSubjects() = userSubjectService.getAllArchivedSubjects()

    @GetMapping("/count",produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun getCountUsers() = userSubjectService.getSubjectCount()

    @GetMapping("/{id}",produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    suspend fun getOneSubject(@PathVariable id: Int) = userSubjectService.getOneSubject(id)

    @GetMapping("/class/{id}",produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    suspend fun getSubjectByClassId(@PathVariable id: Int) = userSubjectService.getSubjectByClassId(id)

    @GetMapping("/teacher/{id}",produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    suspend fun getSubjectsByTeacherId(@PathVariable id: Int) = userSubjectService.getSubjectsByTeacherId(id)

    @PostMapping("/create",consumes = [MediaType.APPLICATION_JSON_VALUE],
            produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    suspend fun createSubject(@RequestBody userSubjectRequest: UserSubjectRequest) = userSubjectService.createSubject(userSubjectRequest)

    @PutMapping("/update",consumes = [MediaType.APPLICATION_JSON_VALUE],
            produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun updateSubject(@RequestBody updateRequest: UserSubjectUpdateRequest) = userSubjectService.updateSubject(updateRequest)

    @PutMapping("/archive",consumes = [MediaType.APPLICATION_JSON_VALUE],
            produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun archiveSubject(@RequestBody archiveSubjectRequest: ArchiveSubjectRequest) = userSubjectService.archiveOneSubject(archiveSubjectRequest)

    @DeleteMapping("/remove",consumes = [MediaType.APPLICATION_JSON_VALUE],
            produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun removeSubject(@RequestBody removeSubjectRequest: RemoveSubjectRequest) = userSubjectService.deleteSubject(removeSubjectRequest)

    @GetMapping("/result/{studentId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun getAverageResultsBySubjects(@PathVariable studentId: Int) = userSubjectService.getAverageResultBySubject(studentId)

}