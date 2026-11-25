package co.com.crediya.model.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
// Enum de codigos de error, que ejercen como "manejo de constantes y asi poder alertar de buena manera acerca del error hallado"
public enum ErrorCode {
    REQUIRED_FIELD("El campo %s es obligatorio"),
    INVALID_EMAIL_FORMAT("El correo electrónico no tiene un formato válido"),
    INVALID_BASE_SALARY("El salario base debe estar entre 0 y 15000000"),
    EMAIL_ALREADY_REGISTERED("El correo electrónico ya está registrado"),
    IDENTITY_DOCUMENT_ALREADY_REGISTERED("El documento de identidad ya está registrado"),
    ROLE_NOT_FOUND("El rol especificado no existe");

    private final String messageTemplate;
}