const redirectionF=(id)=>{
    window.location.href="/contact/id="+id
   }

   const search=()=>{
    const query=document.getElementById("search-input").value;
    try{

        const data=fetch(`http://localhost:8000/search-contact/${query}`);
        data.then(response=>{
           response.json().then(r=>{
            console.log(r);
           });
        })
       




    }
    catch(error){
        console.log(error)
    }
   

   }