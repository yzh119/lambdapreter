(define (map proc seq)
  (if (null? seq) '() (cons (proc (car seq)) (map proc (cdr seq)))))
(define (caar x) (car (car x)))
(define (cadr x) (car (cdr x)))
(define (cadar x) (car (cdr (car x))))
(define (cddr x) (cdr (cdr x)))
(define (reverse lst)
  (if (null? lst) '()
    (append (reverse (cdr lst)) (list (car lst)))))

(define (memq obj lst) 
  (if (null? lst) #f
    (if (eq? obj (car lst))
      lst
      (memq obj (cdr lst)))))
      
(define (length lst)
  (if (null? lst) 0
    (+ 1 (length (cdr lst)))))

(define (list? x)
  (if (null? x) #t
    (if (pair? x)
      (list? (cdr x))
      #f)))
      
(define (list-ref lst k)
  (if (= k 1) 
    (car lst)
    (list-ref (cdr lst) (- k 1))))
    
(define (assq obj alst)
  (if (null? alst) #f
      (if (equal? obj (caar alst)) (car alst) (assq obj (cdr alst))))) 