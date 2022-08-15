package guru.springfamework.controllers.v1;

import guru.springfamework.api.v1.model.VendorDTO;
import guru.springfamework.api.v1.model.VendorDTO;
import guru.springfamework.controllers.RestResponseEntityExceptionHandler;
import guru.springfamework.services.ResourceNotFoundException;
import guru.springfamework.services.VendorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VendorControllerTest extends AbstractRestControllerTest {

    public static final long ID = 1l;
    public static final String NAME = "Home Fruits";
    private static final String NAME2 = "Exotic Fruits Company.";

    @Mock
    VendorService vendorService;

    @InjectMocks
    VendorController vendorController;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(vendorController)
                .setControllerAdvice(new RestResponseEntityExceptionHandler())
                .build();
    }

    @Test
    void testListVendors() throws Exception{
        //given
        VendorDTO vendor1 = new VendorDTO();
        vendor1.setName(NAME);
        vendor1.setVendorUrl(getVendorUri(ID));

        VendorDTO vendor2 = new VendorDTO();
        vendor2.setName("Sam");
        vendor2.setVendorUrl(getVendorUri(2L));

        when(vendorService.getAllVendors()).thenReturn(Arrays.asList(vendor1, vendor2));
        ResultActions resultActions =
        mockMvc.perform(get(VendorController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON));
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vendors", hasSize(2)));
    }

    @Test
    void testGetVendorById() throws Exception{
        //given
        VendorDTO vendor1 = new VendorDTO();
        vendor1.setName(NAME);
        vendor1.setVendorUrl(getVendorUri(ID));

        when(vendorService.getVendorById(anyLong())).thenReturn(vendor1);

        //when
        mockMvc.perform(get(getVendorUri(ID))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo(NAME)));
    }

    @Test
    public void testGetVendorByIdNotFound() throws Exception {
        when(vendorService.getVendorById(anyLong())).thenThrow(ResourceNotFoundException.class);

        //when
        mockMvc.perform(get(getVendorUri(ID))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateNewVendor() throws Exception{
        //given
        VendorDTO vendor = new VendorDTO();
        vendor.setName(NAME2);

        VendorDTO returnDTO = new VendorDTO();
        returnDTO.setName(vendor.getName());
        returnDTO.setVendorUrl(getVendorUri(ID));

        when(vendorService.createNewVendor(vendor)).thenReturn(returnDTO);

        //when/then
        mockMvc.perform(post(VendorController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(vendor)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", equalTo(NAME2)))
                .andExpect(jsonPath("$.vendor_url", equalTo(getVendorUri(ID))));
    }

    @Test
    void testUpdateVendor() throws Exception{
        //given
        VendorDTO vendor = new VendorDTO();
        vendor.setName(NAME2);

        VendorDTO returnDTO = new VendorDTO();
        returnDTO.setName(vendor.getName());
        returnDTO.setVendorUrl(getVendorUri(ID));

        when(vendorService.saveVendorByDTO(anyLong(), any(VendorDTO.class))).thenReturn(returnDTO);

        //when/then
        mockMvc.perform(put(getVendorUri(ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(vendor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo(NAME2)))
                .andExpect(jsonPath("$.vendor_url", equalTo(getVendorUri(ID))));
    }

    @Test
    void testPatchVendor() throws Exception{
        //given
        VendorDTO vendor = new VendorDTO();
        vendor.setName(NAME2);

        VendorDTO returnDTO = new VendorDTO();
        returnDTO.setName(vendor.getName());
        returnDTO.setVendorUrl(getVendorUri(ID));

        when(vendorService.patchVendor(anyLong(), any(VendorDTO.class))).thenReturn(returnDTO);

        //when/then
        mockMvc.perform(patch(getVendorUri(ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(vendor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo(NAME2)))
                .andExpect(jsonPath("$.vendor_url", equalTo(getVendorUri(ID))));
    }

    @Test
    void deleteVendor() throws Exception{
        mockMvc.perform(delete(getVendorUri(ID)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(vendorService).deleteVendorByID(anyLong());
    }

    private String getVendorUri(long id) {
        return VendorController.BASE_URL + id;
    }
}