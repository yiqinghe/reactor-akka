package feignserver1;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeignServer {

    @RequestMapping(method = RequestMethod.GET,value = "/test1")
    public Integer test1(@RequestParam("number") Integer number){
        return number+1;
    }
}
