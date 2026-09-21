package com.volna.restaurantservice.storage;
import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Service; import org.springframework.web.multipart.MultipartFile; import java.io.*; import java.nio.file.*; import java.util.UUID;
@Service public class LocalStorageService implements StorageService{
 private final Path path;
 public LocalStorageService(@Value("${app.storage.upload-dir:uploads}")String dir){
  path=Paths.get(dir).toAbsolutePath().normalize();
 }
 public String store(MultipartFile f)throws IOException{
  Files.createDirectories(path);
  String n=f.getOriginalFilename()==null?"file":Paths
          .get(f.getOriginalFilename()).getFileName().toString();
  String name=UUID.randomUUID()+"-"+n;Path target=path.resolve(name).normalize();
  if(!target.startsWith(path))
   throw new IOException("Invalid path");
  Files.copy(f.getInputStream(),target,StandardCopyOption.REPLACE_EXISTING);
  return "/uploads/"+name;}
}