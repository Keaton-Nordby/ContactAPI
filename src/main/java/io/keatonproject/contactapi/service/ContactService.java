package io.keatonproject.contactapi.service;

import io.keatonproject.contactapi.domain.Contact;
import io.keatonproject.contactapi.repo.ContactRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor

public class ContactService {
    private final ContactRepo contactRepo;

    public Page<Contact > getallContacts(int page, int size) {
        return contactRepo.findAll(PageRequest.of(page, size, Sort.by("name")));
    }

    public Contact getContactById(String id) {
        return contactRepo.findById(id).orElseThrow(() -> new RuntimeException("Contact not found"));
    }

    public Contact createContact(Contact contact) {
        return contactRepo.save(contact);
    }

    public void deleteContact(Contact contact) {
        // Assignment
    }

    public String uploadPhoto(String id, MultipartFile file) {
        Contact contact = getContactById(id);
        String photoUrl = null;
        contact.setPhotoUrl(photoUrl);
        contactRepo.save(contact);
        return photoUrl;
    }

    private final Function<String, String> fileExtension = filename -> Optional.of(filename).filter(name -> name.contains("."))
            .map(name ->"." + name.substring(filename.lastIndexOf(".") + 1)).orElse(".png");

    private final BiFunction<String, MultipartFile, String> photoFunction = (id, image) -> {
        try {
            Path fileStoragelocation = Paths.get("").toAbsolutePath().normalize();
            if(!Files.exists(fileStoragelocation)) { Files.createDirectories(fileStoragelocation); }
            Files.copy(image.getInputStream(), fileStoragelocation.resolve(id + ".png"), REPLACE_EXISTING);
        } catch (Exception exception) {
            throw new RuntimeException("Unable to save image");
        }
    };
}
