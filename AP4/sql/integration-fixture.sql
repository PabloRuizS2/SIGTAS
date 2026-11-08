-- SOLO para sigtas_ap4_it, después de cargar schema.sql/data.sql con ese nombre.
-- Ejecutar con la cuenta administradora de la instancia de pruebas.
USE sigtas_ap4_it;
DELIMITER //
CREATE TRIGGER fallo_auditoria BEFORE INSERT ON auditoria FOR EACH ROW
BEGIN
    IF NEW.id_usuario = 4 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Fallo controlado de auditoria';
    END IF;
END//
DELIMITER ;
