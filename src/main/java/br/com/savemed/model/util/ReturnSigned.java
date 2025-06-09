package br.com.savemed.model.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReturnSigned {

     Long idLastPage;
     byte[] fileData;
}
