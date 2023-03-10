package shop.mtcoding.blog.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import shop.mtcoding.blog.dto.ResponseDto;
import shop.mtcoding.blog.dto.board.BoardReqDto.BoardSaveReqDto;
import shop.mtcoding.blog.dto.board.BoardReqDto.BoardUpdateReqDto;
import shop.mtcoding.blog.dto.board.BoardRespDto.BoardUpdateRespDto;
import shop.mtcoding.blog.handler.exception.CustomApiException;
import shop.mtcoding.blog.handler.exception.CustomException;
import shop.mtcoding.blog.model.BoardRepository;
import shop.mtcoding.blog.model.User;
import shop.mtcoding.blog.service.BoardService;

@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;

    @Autowired
    private HttpSession session;

    @Autowired
    private BoardRepository boardRepository;

    @GetMapping({ "/", "/board" })
    public String main(Model model) {
        model.addAttribute("dtos", boardRepository.findAllWithUser());
        return "/board/main";
    }

    @GetMapping("/board/detail")
    public String detail() {
        return "/board/detail";
    }

    @GetMapping("/board/{id}")
    public String detail(@PathVariable int id, Model model) {
        model.addAttribute("boardDto", boardRepository.findByIdWithUser(id));
        return "/board/detail";
    }

    @GetMapping("/board/saveForm")
    public String saveForm() {
        return "/board/saveForm";
    }

    @PostMapping("/board")
    public @ResponseBody ResponseEntity<?> save(@RequestBody BoardSaveReqDto boardSaveReqDto) {
        User principal = (User) session.getAttribute("principal");
        if (principal == null) {
            throw new CustomApiException("????????? ??????????????????", HttpStatus.UNAUTHORIZED);
        }
        if (boardSaveReqDto.getTitle() == null ||
                boardSaveReqDto.getTitle().isEmpty()) {
            throw new CustomApiException("????????? ??????????????????");
        }

        if (boardSaveReqDto.getTitle().length() > 100) {
            throw new CustomApiException("????????? ????????? 100??? ???????????? ???????????????");
        }

        if (boardSaveReqDto.getContent() == null ||
                boardSaveReqDto.getContent().isEmpty()) {
            throw new CustomApiException("??? ????????? ??????????????????");
        }

        boardService.?????????(boardSaveReqDto, principal.getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "????????? ??????", null), HttpStatus.CREATED);
    }

    @DeleteMapping("/board/{id}")
    public @ResponseBody ResponseEntity<?> delete(@PathVariable int id) {
        User principal = (User) session.getAttribute("principal");
        if (principal == null) {
            throw new CustomApiException("????????? ??????????????????", HttpStatus.UNAUTHORIZED);
        }

        boardService.???????????????(id, principal.getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "?????? ??????", null), HttpStatus.OK);
    }

    // id??? ????????? ??????
    @GetMapping("/board/{id}/updateForm")
    public String boardUpdateForm(@PathVariable int id, Model model) {
        User principal = (User) session.getAttribute("principal");
        if (principal == null) {
            throw new CustomException("????????? ??????????????????", HttpStatus.UNAUTHORIZED);
        }
        BoardUpdateRespDto dto = boardRepository.findByIdForUpdate(id);
        if (dto == null) {
            throw new CustomException("???????????? ?????? ??????????????????");
        }
        if (principal.getId() != dto.getUserId()) {
            throw new CustomException("?????? ???????????? ????????? ????????? ????????????", HttpStatus.FORBIDDEN);
        }
        model.addAttribute("dto", dto);
        return "/board/boardUpdateForm";
    }

    @PutMapping("/board/{id}")
    public @ResponseBody ResponseEntity<?> update(@PathVariable int id,
            @RequestBody BoardUpdateReqDto boardUpdateReqDto) {
        User principal = (User) session.getAttribute("principal");
        if (principal == null) {
            throw new CustomApiException("????????? ??????????????????", HttpStatus.UNAUTHORIZED);
        }

        if (boardUpdateReqDto.getTitle() == null ||
                boardUpdateReqDto.getTitle().isEmpty()) {
            throw new CustomApiException("????????? ??????????????????");
        }

        if (boardUpdateReqDto.getTitle().length() > 100) {
            throw new CustomApiException("????????? ????????? 100??? ???????????? ???????????????");
        }

        if (boardUpdateReqDto.getContent() == null ||
                boardUpdateReqDto.getContent().isEmpty()) {
            throw new CustomApiException("??? ????????? ??????????????????");
        }

        boardService.???????????????(id, boardUpdateReqDto, principal.getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "????????? ?????? ??????", null), HttpStatus.OK);
    }

}
