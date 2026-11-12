package co.ufps.clinica.adapters.clinicalrecord;

public record AttachmentUploadRequest(String fileName, String contentType, String contentBase64) {}
