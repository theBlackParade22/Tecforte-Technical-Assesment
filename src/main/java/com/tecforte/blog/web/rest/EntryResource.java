package com.tecforte.blog.web.rest;

import com.tecforte.blog.domain.enumeration.Emoji;
import com.tecforte.blog.service.BlogService;
import com.tecforte.blog.service.EntryService;
import com.tecforte.blog.service.dto.BlogDTO;
import com.tecforte.blog.service.dto.EntryDTO;
import com.tecforte.blog.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.tecforte.blog.domain.Entry}.
 */
@RestController
@RequestMapping("/api")
public class EntryResource {

    private static final String ENTITY_NAME = "entry";
    private final Logger log = LoggerFactory.getLogger(EntryResource.class);
    private final EntryService entryService;
    private final BlogService blogService;
    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public EntryResource(EntryService entryService, BlogService blogService) {
        this.entryService = entryService;
        this.blogService = blogService;
    }

    /**
     * {@code POST  /entries} : Create a new entry.
     *
     * @param entryDTO the entryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new entryDTO, or with status {@code 400 (Bad Request)} if the entry has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/entries")
    public ResponseEntity<EntryDTO> createEntry(@Valid @RequestBody EntryDTO entryDTO) throws URISyntaxException {
        log.debug("REST request to save Entry : {}", entryDTO);
        if (entryDTO.getId() != null) {
            throw new BadRequestAlertException("A new entry cannot already have an ID", ENTITY_NAME, "idexists");
        }
        final Optional<Boolean> booleanIsPositive = blogService.findOne(entryDTO.getBlogId()).map(BlogDTO::isPositive);
        booleanIsPositive.ifPresent(isPositive -> {
            final Emoji entryDTOEmoji = entryDTO.getEmoji();
            if (isPositive && (entryDTOEmoji.equals(Emoji.SAD) || entryDTOEmoji.equals(Emoji.ANGRY))) {
                throw new BadRequestAlertException("Invalid Emoji", ENTITY_NAME, "invalidEmoji");
            } else if (!isPositive && (entryDTOEmoji.equals(Emoji.LIKE) || entryDTOEmoji.equals(Emoji.HAHA))) {
                throw new BadRequestAlertException("Invalid Emoji", ENTITY_NAME, "invalidEmoji");
            } else {
                final String titleToLowerCase = entryDTO.getTitle().toLowerCase();
                final String contentToLowerCase = entryDTO.getContent().toLowerCase();
                if (isPositive && (titleToLowerCase.matches("(sad)|(fear)|(lonely)") || contentToLowerCase.matches("(sad)|(fear)|(lonely)"))) {
                    throw new BadRequestAlertException("Invalid Content", ENTITY_NAME, "invalidContent");
                } else if (!isPositive && (titleToLowerCase.matches("(love)|(happy)|(trust)") || contentToLowerCase.matches("(love)|(happy)|(trust)"))) {
                    throw new BadRequestAlertException("Invalid Content", ENTITY_NAME, "invalidContent");
                }
            }
        });
        EntryDTO result = entryService.save(entryDTO);

        return ResponseEntity.created(new URI("/api/entries/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /entries} : Updates an existing entry.
     *
     * @param entryDTO the entryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated entryDTO,
     * or with status {@code 400 (Bad Request)} if the entryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the entryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/entries")
    public ResponseEntity<EntryDTO> updateEntry(@Valid @RequestBody EntryDTO entryDTO) throws URISyntaxException {
        log.debug("REST request to update Entry : {}", entryDTO);
        if (entryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        final Optional<Boolean> booleanIsPositive = blogService.findOne(entryDTO.getBlogId()).map(BlogDTO::isPositive);
        booleanIsPositive.ifPresent(isPositive -> {
            final Emoji entryDTOEmoji = entryDTO.getEmoji();
            if (isPositive && (entryDTOEmoji.equals(Emoji.SAD) || entryDTOEmoji.equals(Emoji.ANGRY))) {
                throw new BadRequestAlertException("Invalid Emoji", ENTITY_NAME, "invalidEmoji");
            } else if (!isPositive && (entryDTOEmoji.equals(Emoji.LIKE) || entryDTOEmoji.equals(Emoji.HAHA))) {
                throw new BadRequestAlertException("Invalid Emoji", ENTITY_NAME, "invalidEmoji");
            } else {
                final String titleToLowerCase = entryDTO.getTitle().toLowerCase();
                final String contentToLowerCase = entryDTO.getContent().toLowerCase();
                if (isPositive && (titleToLowerCase.matches("(sad)|(fear)|(lonely)") || contentToLowerCase.matches("(sad)|(fear)|(lonely)"))) {
                    throw new BadRequestAlertException("Invalid Content", ENTITY_NAME, "invalidContent");
                } else if (!isPositive && (titleToLowerCase.matches("(love)|(happy)|(trust)") || contentToLowerCase.matches("(love)|(happy)|(trust)"))) {
                    throw new BadRequestAlertException("Invalid Content", ENTITY_NAME, "invalidContent");
                }
            }
        });
        EntryDTO result = entryService.save(entryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, entryDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /entries} : get all the entries.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of entries in body.
     */
    @GetMapping("/entries")
    public ResponseEntity<List<EntryDTO>> getAllEntries(Pageable pageable) {
        log.debug("REST request to get a page of Entries");
        Page<EntryDTO> page = entryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /entries/:id} : get the "id" entry.
     *
     * @param id the id of the entryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the entryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/entries/{id}")
    public ResponseEntity<EntryDTO> getEntry(@PathVariable Long id) {
        log.debug("REST request to get Entry : {}", id);
        Optional<EntryDTO> entryDTO = entryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(entryDTO);
    }

    /**
     * {@code DELETE  /entries/:id} : delete the "id" entry.
     *
     * @param id the id of the entryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/entries/{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long id) {
        log.debug("REST request to delete Entry : {}", id);
        entryService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }


}
