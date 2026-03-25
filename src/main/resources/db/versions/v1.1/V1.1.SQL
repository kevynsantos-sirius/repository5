create table CL_logomodelotipo(idLogomodeloTipo int IDENTITY(1,1) primary key, codigo int, descricao varchar(20));

INSERT INTO CL_logomodelotipo (codigo, descricao) VALUES
(1, 'Logo'),
(2, 'Arquivo Adicional'),
(3, 'Assinatura');

CREATE TABLE CL_Logomodelo (
    idLogoModelo INT IDENTITY(1,1) PRIMARY KEY,
    idModelo INT NOT NULL,              -- FK para CL_ModeloDocumento(Id)
    codigo INT NOT NULL,

    CONSTRAINT FK_Logomodelo_ModeloDocumento
        FOREIGN KEY (idModelo)
        REFERENCES CL_ModeloDocumento(Id)
);

