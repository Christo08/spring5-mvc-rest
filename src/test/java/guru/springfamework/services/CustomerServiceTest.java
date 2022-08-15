package guru.springfamework.services;

import guru.springfamework.api.v1.mapper.CustomerMapper;
import guru.springfamework.api.v1.model.CustomerDTO;
import guru.springfamework.controllers.v1.CustomerController;
import guru.springfamework.domain.Customer;
import guru.springfamework.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    public static final long ID = 1l;
    public static final String FIRST_NAME = "Michale";
    public static final String LAST_NAME = "Weston";
    public static final String FIRST_NAME2 = "Jim";
    CustomerService customerService;

    @Mock
    CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        customerService = new CustomerServiceImpl(CustomerMapper.INSTANCE, customerRepository);
    }

    @Test
    void getAllCustomers() throws Exception {
        List<Customer> categories = Arrays.asList(new Customer(),new Customer(),new Customer());

        when(customerRepository.findAll()).thenReturn(categories);

        List<CustomerDTO> customerDTOS = customerService.getAllCustomers();

        assertEquals(3, customerDTOS.size());
    }

    @Test
    public void getCustomerById() throws Exception {
        //given
        Customer customer1 = new Customer();
        customer1.setId(ID);
        customer1.setFirstName(FIRST_NAME);
        customer1.setLastName(LAST_NAME);

        when(customerRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(customer1));

        //when
        CustomerDTO customerDTO = customerService.getCustomerById(ID);

        assertEquals(FIRST_NAME, customerDTO.getFirstName());
    }

    @Test
    public void createNewCustomer() throws Exception {

        //given
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setFirstName(FIRST_NAME2);

        Customer savedCustomer = new Customer();
        savedCustomer.setFirstName(customerDTO.getFirstName());
        savedCustomer.setLastName(customerDTO.getLastName());
        savedCustomer.setId(ID);

        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        //when
        CustomerDTO savedDto = customerService.createNewCustomer(customerDTO);

        //then
        assertEquals(customerDTO.getFirstName(), savedDto.getFirstName());
        assertEquals(getCustomerUri(ID), savedDto.getCustomerUrl());
    }

    @Test
    public void saveCustomerByDTO() throws Exception {
        //given
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setFirstName(FIRST_NAME2);

        Customer savedCustomer = new Customer();
        savedCustomer.setFirstName(customerDTO.getFirstName());
        savedCustomer.setLastName(customerDTO.getLastName());
        savedCustomer.setId(ID);

        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        //when
        CustomerDTO savedDto = customerService.saveCustomerByDTO(ID, customerDTO);

        //then
        assertEquals(customerDTO.getFirstName(), savedDto.getFirstName());
        assertEquals(getCustomerUri(ID), savedDto.getCustomerUrl());
    }

    @Test
    void patchCustomer() throws Exception{
        //given
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setFirstName(FIRST_NAME2);

        Customer updateCustomer = new Customer();
        updateCustomer.setFirstName(customerDTO.getFirstName());
        updateCustomer.setLastName(LAST_NAME);
        updateCustomer.setId(ID);

        Customer oldCustomer = new Customer();
        oldCustomer.setFirstName(FIRST_NAME);
        oldCustomer.setLastName(LAST_NAME);
        oldCustomer.setId(ID);

        when(customerRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(oldCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(updateCustomer);

        //when
        CustomerDTO savedDto = customerService.patchCustomer(ID, customerDTO);

        //then
        assertEquals(updateCustomer.getFirstName(), savedDto.getFirstName());
        assertEquals(oldCustomer.getFirstName(), savedDto.getFirstName());
        assertEquals(getCustomerUri(ID), savedDto.getCustomerUrl());
    }

    @Test
    public void deleteCustomerById() throws Exception{
        Long id =ID;

        customerRepository.deleteById(id);

        verify(customerRepository, times(1)).deleteById(anyLong());
    }

    private String getCustomerUri(Long Id){
        return CustomerController.BASE_URL + Id;
    }

}